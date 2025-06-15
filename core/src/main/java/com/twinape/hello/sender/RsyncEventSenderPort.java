package com.twinape.hello.sender;
import com.twinape.mood.social.rsync.client.RsyncEvent;

import java.util.concurrent.CompletionStage;

public interface RsyncEventSenderPort {
    CompletionStage<Void> publish(RsyncEvent event);
}
