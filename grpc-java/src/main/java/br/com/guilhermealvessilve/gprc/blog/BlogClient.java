package br.com.guilhermealvessilve.gprc.blog;

import br.com.proto.blog.Blog;
import br.com.proto.blog.BlogId;
import br.com.proto.blog.BlogServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BlogClient {

    public static void main(String[] args) throws InterruptedException {

        var channel = ManagedChannelBuilder.forAddress("localhost", 50_051)
                .usePlaintext()
                .build();

        run(channel);

        LOG.info("Shutting down!");
        channel.shutdown();
        channel.awaitTermination(3, TimeUnit.SECONDS);
    }

    private static void run(ManagedChannel channel) {
        var stub = BlogServiceGrpc.newBlockingStub(channel);
        var blogId = createBlog(stub);
        if (null == blogId) return;
        var blog = readBlog(blogId, stub);
        if (null == blog) return;
        updateBlog(blogId, stub);
        listBlogs(stub);
        deleteBlog(blogId, stub);
    }

    private static BlogId createBlog(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        try {
            var blogId = stub.createBlog(Blog.newBuilder()
                    .setAuthor("John Doe")
                    .setTitle("New Blog!")
                    .setContent("Hello World this is a new blog!")
                    .build());
            LOG.info("Blog created: " + blogId.getId());
            return blogId;
        } catch (StatusRuntimeException ex) {
            LOG.error("Couldn't create the blog: ", ex);
            return null;
        }
    }

    private static Blog readBlog(BlogId blogId, BlogServiceGrpc.BlogServiceBlockingStub stub) {
        try {
            var blog = stub.readBlog(blogId);
            LOG.info("Blog read: " + blog);
            return blog;
        } catch (StatusRuntimeException ex) {
            LOG.error("Couldn't read the blog: ", ex);
            return null;
        }
    }

    private static BlogId updateBlog(BlogId blogId, BlogServiceGrpc.BlogServiceBlockingStub stub) {
        try {
            var modifiedBlog = Blog.newBuilder()
                    .setId(blogId.getId())
                    .setAuthor("John Claudio Van Cloud")
                    .setTitle("Some awesome content!")
                    .setContent("Nani?")
                    .build();
            stub.updateBlog(modifiedBlog);
            LOG.info("Blog update: " + modifiedBlog);
            return blogId;
        } catch (StatusRuntimeException ex) {
            LOG.error("Couldn't update the blog: ", ex);
            return null;
        }
    }

    private static void listBlogs(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        try {
            var it = stub.listBlogs(Empty.getDefaultInstance());
            var blogs = new LinkedList<Blog>();
            it.forEachRemaining(blogs::add);
            LOG.info("Blog list: " + blogs);
        } catch (StatusRuntimeException ex) {
            LOG.error("Couldn't list blogs: ", ex);
        }
    }

    private static void deleteBlog(BlogId blogId, BlogServiceGrpc.BlogServiceBlockingStub stub) {
        try {
            stub.deleteBlog(blogId);
            LOG.info("Blog deleted");
        } catch (StatusRuntimeException ex) {
            LOG.error("Couldn't delete the blog: ", ex);
        }
    }
}
