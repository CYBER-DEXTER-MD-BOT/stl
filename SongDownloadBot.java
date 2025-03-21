import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.io.File;
import java.io.IOException;

public class SongDownloadBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String songRequest = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            
            // Fetch song file using yt-dlp
            File songFile = fetchSong(songRequest);
            
            if (songFile != null) {
                sendSong(chatId, songFile);
            } else {
                sendMessage(chatId, "Song not found.");
            }
        }
    }

    private File fetchSong(String songRequest) {
        try {
            String command = "yt-dlp -x --audio-format mp3 -o 'downloaded_song.%(ext)s' ytsearch1:\"" + songRequest + "\"";
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            File songFile = new File("downloaded_song.mp3");
            return songFile.exists() ? songFile : null;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendSong(long chatId, File songFile) {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(chatId);
        sendAudio.setAudio(songFile);
        try {
            execute(sendAudio);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(long chatId, String message) {
        try {
            execute(new SendMessage(String.valueOf(chatId), message));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return System.getenv("TELEGRAM_BOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("TELEGRAM_BOT_TOKEN");
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new SongDownloadBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
