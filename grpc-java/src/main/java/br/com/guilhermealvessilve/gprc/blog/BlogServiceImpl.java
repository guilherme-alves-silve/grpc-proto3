package br.com.guilhermealvessilve.gprc.blog;

import br.com.proto.blog.Blog;
import br.com.proto.blog.BlogId;
import br.com.proto.blog.BlogServiceGrpc;
import com.google.protobuf.Empty;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

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
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(ex.getLocalizedMessage())
                    .asRuntimeException());
            return;
        }

        if (!result.wasAcknowledged()) {
            responseObserver.onError(Status.INTERNAL
                            .withDescription("Blog couldn't be created!")
                            .asRuntimeException());
            return;
        }

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

    }

    @Override
    public void updateBlock(Blog request, StreamObserver<Empty> responseObserver) {

    }

    @Override
    public void deleteBlog(BlogId request, StreamObserver<Empty> responseObserver) {

    }

    @Override
    public void listBlogs(Empty request, StreamObserver<Blog> responseObserver) {

    }
}
