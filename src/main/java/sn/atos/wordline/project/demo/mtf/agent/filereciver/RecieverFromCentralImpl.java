package sn.atos.wordline.project.demo.mtf.agent.filereciver;

import akka.actor.ActorRef;
import akka.stream.Materializer;
import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class RecieverFromCentralImpl implements FileTransfertManager {
    private final  Materializer mat;
    private final  ActorRef transfertManagerActor ;
    private final  FileTransfertManagerClient client;

    public RecieverFromCentralImpl(Materializer mat , ActorRef transfertManagerActor , FileTransfertManagerClient client ) {
        this.mat = mat;
        this.transfertManagerActor=transfertManagerActor;
        this.client=client ;
    }
    @Override
    public CompletionStage<PostFileActionPerformed> postFile(filePart in) {
        transfertManagerActor.tell(in, transfertManagerActor.noSender());
        PostFileActionPerformed _PostFileActionPerformed = PostFileActionPerformed
                .newBuilder()
                .setErrorMessage("")
                .setHasError(true)
                .setMessage("ok")
                .build();
        return CompletableFuture.completedFuture(_PostFileActionPerformed);
    }

    @Override
    public CompletionStage<PostFileInfoActionPerformed> postFileinfo(fileInfo in) {
        transfertManagerActor.tell(in, transfertManagerActor.noSender());
        PostFileInfoActionPerformed _postFileInfoActionPerformed = PostFileInfoActionPerformed
                .newBuilder()
                .setErrorMessage("")
                .setHasError(true)
                .setMessage("ok")
                .build();
        return CompletableFuture.completedFuture(_postFileInfoActionPerformed);
    }
}
