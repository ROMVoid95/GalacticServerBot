//package io.github.romvoid95.commands.ctx;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.apache.commons.codec.digest.DigestUtils;
//
//import io.github.readonly.command.ctx.MessageContextMenu;
//import io.github.readonly.command.event.MessageContextMenuEvent;
//import io.github.romvoid95.GalacticBot;
//import io.github.romvoid95.util.Embed;
//import io.github.romvoid95.util.HttpClient;
//import lombok.extern.slf4j.Slf4j;
//import net.dv8tion.jda.api.entities.Message.Attachment;
//
//@Slf4j
//public class ToHastebinContext extends MessageContextMenu
//{
//
//    public ToHastebinContext()
//    {
//        this.name = "Send To Hastebin";
//    }
//    
//    @Override
//    protected void execute(MessageContextMenuEvent event)
//    {
//        final List<Attachment> files = event.getTarget().getAttachments();
//        
//        if(files.isEmpty())
//        {
//            event.reply("Message does not contain any files").queue();
//        } else {
//            Map<String, String> fileMap = new HashMap<>();
//            event.deferReply(true).queue();
//            
//            files.forEach(file -> {
//                String fileName = file.getFileName();
//                file.getProxy().download().thenAccept(in -> {
//                    StringBuilder builder = new StringBuilder();
//                    byte[] buf = new byte[1024];
//                    int count = 0;
//                    
//                    try {
//                        while ((count = in.read(buf)) > 0) {
//                            builder.append(new String(buf, 0, count));
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        in.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    
//                    String sha256hex = DigestUtils.md5Hex(builder.toString());
//                    log.info(sha256hex);
//                    
//                    //String paste = HttpClient.paste(builder.toString());
//                    fileMap.put(fileName, sha256hex);
//                });
//            });
//            Embed e = Embed.newBuilder();
//            for(Entry<String, String> entry : fileMap.entrySet())
//            {
//                e.field(entry.getKey(), entry.getValue());
//            }
//            event.getHook().sendMessageEmbeds(e.toEmbed()).queue();
//        }
//    }
//}
