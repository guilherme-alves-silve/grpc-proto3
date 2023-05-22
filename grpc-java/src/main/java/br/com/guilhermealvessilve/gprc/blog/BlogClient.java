package br.com.guilhermealvessilve.gprc.blog;

import br.com.proto.blog.Blog;
import br.com.proto.blog.BlogId;
import br.com.proto.blog.BlogServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlogClient {

    private static final int NO_OPTION_SELECTED = -1;

    public static void main(String[] args) throws InterruptedException {

        var channel = ManagedChannelBuilder.forAddress("localhost", 50_051)
                .usePlaintext()
                .build();

        run(channel);

        LOG.info("Shutting down!");
        channel.shutdown();
    }

    private static void run(ManagedChannel channel) {
        var stub = BlogServiceGrpc.newBlockingStub(channel);
        var blogId = createBlog(stub);
        if (null == blogId) return;
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
}
