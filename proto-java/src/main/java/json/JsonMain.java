package json;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import example.simple.SimpleOuterClass;

import java.util.List;

public class JsonMain {

    public static void main(String[] args) throws InvalidProtocolBufferException {
        SimpleOuterClass.Simple message = SimpleOuterClass.Simple.newBuilder()
                .setId(42)
                .setName("Some name")
                .setIsSimple(true)
                .addAllSampleList(List.of(1, 2, 3))
                .build();

        var json = toJSON(message);
        System.out.println(json);

        var decodedMessage = fromJSON(json);
        System.out.println(decodedMessage);
    }

    private static String toJSON(SimpleOuterClass.Simple message) throws InvalidProtocolBufferException {
        return JsonFormat.printer()
                .omittingInsignificantWhitespace()
                .print(message);
    }

    private static SimpleOuterClass.Simple fromJSON(String json) throws InvalidProtocolBufferException {
        var builder = SimpleOuterClass.Simple.newBuilder();
        JsonFormat.parser()
                .merge(json, builder);
        return builder.build();
    }
}
