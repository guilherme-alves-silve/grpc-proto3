package br.com.guilhermealvessilve.gprc.blog;

import br.com.proto.blog.Blog;
import br.com.proto.blog.BlogId;
import br.com.proto.blog.BlogServiceGrpc;
import com.google.protobuf.Empty;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@Slf4j
public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private final MongoCollection<Document> mongoCollection;

    public BlogServiceImpl(MongoClient client) {
        MongoDatabase db = client.getDatabase("blogdb");
        mongoCollection = db.getCollection("blog");
    }

    @Override
    public void createBlog(Blog request, StreamObserver<BlogId> responseObserver) {
        var doc = new Document("author", request.getAuthor())
                .append("title", request.getTitle())
                .append("content", request.getContent());

        InsertOneResult result;
        try {
            result = mongoCollection.insertOne(doc);
        } catch (MongoException ex) {
            LOG.error("Error: ", ex);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Blog couldn't be created!")
                    .asRuntimeException());
            return;
        }

        if (hasNotAcknowledgedSetError(result.wasAcknowledged(), responseObserver)) return;
        if (null == result.getInsertedId()) return;

        String id = result.getInsertedId()
                .asObjectId()
                .getValue()
                .toString();
        responseObserver.onNext(BlogId.newBuilder()
                        .setId(id)
                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(BlogId request, StreamObserver<Blog> responseObserver) {
        if (hasBlankIdSentError(request.getId(), responseObserver)) return;

        var id = request.getId();
        var document = mongoCollection.find(eq("_id", new ObjectId(id))).first();
        if (hasNotFoundSentNotFound(id, document, responseObserver)) return;

        responseObserver.onNext(Blog.newBuilder()
                        .setAuthor(document.getString("author"))
                        .setTitle(document.getString("title"))
                        .setContent(document.getString("content"))
                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateBlog(Blog request, StreamObserver<Empty> responseObserver) {
        if (hasBlankIdSentError(request.getId(), responseObserver)) return;

        var id = request.getId();
        var document = mongoCollection.findOneAndUpdate(
                eq("_id", new ObjectId(id)),
                combine(
                    set("author", request.getAuthor()),
                    set("title", request.getTitle()),
                    set("content", request.getContent())
                ));

        if (hasNotFoundSentNotFound(id, document, responseObserver)) return;

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBlog(BlogId request, StreamObserver<Empty> responseObserver) {
        if (hasBlankIdSentError(request.getId(), responseObserver)) return;

        var id = request.getId();
        DeleteResult result;
        try {
            result = mongoCollection.deleteOne(eq("_id", new ObjectId(id)));
        } catch (MongoException ex) {
            LOG.error("Error: ", ex);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Blog couldn't be deleted!")
                    .asRuntimeException());
            return;
        }

        if (hasNotAcknowledgedSetError(result.wasAcknowledged(), responseObserver)) return;
        if (result.getDeletedCount() == 0) {
            sendNotFound(id, responseObserver);
            return;
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void listBlogs(Empty request, StreamObserver<Blog> responseObserver) {
        mongoCollection.find().forEach(document -> responseObserver.onNext(Blog.newBuilder()
                        .setId(document.getObjectId("_id").toString())
                        .setAuthor(document.getString("author"))
                        .setTitle(document.getString("title"))
                        .setContent(document.getString("content"))
                        .build()));
        responseObserver.onCompleted();
    }

    private static boolean hasNotFoundSentNotFound(String id, Document document, StreamObserver<?> responseObserver) {
        if (null == document) {
            sendNotFound(id, responseObserver);
            return true;
        }

        return false;
    }

    private static void sendNotFound(String id, StreamObserver<?> responseObserver) {
        responseObserver.onError(Status.NOT_FOUND
                .withDescription("Blog was not found")
                .augmentDescription("BlogId: " + id)
                .asRuntimeException());
    }

    private static boolean hasBlankIdSentError(String id, StreamObserver<?> responseObserver) {
        if (id.isBlank()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("The blog ID cannot be blank!")
                    .asRuntimeException());
            return true;
        }

        return false;
    }

    private static boolean hasNotAcknowledgedSetError(boolean acknowledged,
                                                      StreamObserver<?> responseObserver) {
        if (!acknowledged) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Blog couldn't be created!")
                    .asRuntimeException());
            return true;
        }

        return false;
    }
}
