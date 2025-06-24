package org.example.command.Action;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.command.Command;
import org.example.postgres.entity.State;
import org.example.postgres.repository.UserRepository;


public class GetToken implements Command {
    @Override
    public SendMessage apply(Update update, UserRepository repository) {
        Long id = update.message().chat().id();
        String message = update.message().text();
        try {
            ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
            if(!message.equals("Отмена")){
                repository.register(id,message);
                repository.setState(id, State.NONE);
                return new SendMessage(id, "Вы зарегистрировались").replyMarkup(remove);
            }
            else{
                repository.setState(id, State.NONE);
                return new SendMessage(id, "Вы вернулись к меню").replyMarkup(remove);
            }
        } catch (Exception e) {
            return new SendMessage(id, "Токен не валидный попробуйте снова");
        }
    }
}
