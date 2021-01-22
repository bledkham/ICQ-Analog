package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BotClient extends Client{

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    @Override
    protected String getUserName() {
        String userName = "date_bot_"+(int) (Math.random()*100);
        return userName;
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    public class BotSocketThread extends Client.SocketThread{

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
            if(message == null) return;

            String[] s = message.split(": ");
            if(s.length != 2){
                return;
            }
            String userName = s[0];
            String command = s[1].trim();

            SimpleDateFormat sdf;
            switch (command){
                case "дата":
                    sdf = new SimpleDateFormat("d.MM.YYYY");
                    break;
                case "день":
                    sdf = new SimpleDateFormat("d");
                    break;
                case "месяц":
                    sdf = new SimpleDateFormat("MMMM");
                    break;
                case "год":
                    sdf = new SimpleDateFormat("YYYY");
                    break;
                case "время":
                    sdf = new SimpleDateFormat("H:mm:ss");
                    break;
                case "час":
                    sdf = new SimpleDateFormat("H");
                    break;
                case "минуты":
                    sdf = new SimpleDateFormat("m");
                    break;
                case "секунды":
                    sdf = new SimpleDateFormat("s");
                    break;
                default:
                    return;
            }
            Date date = Calendar.getInstance().getTime();
            sendTextMessage(String.format("Информация для %s: %s", userName, sdf.format(date)));
        }
    }
}
