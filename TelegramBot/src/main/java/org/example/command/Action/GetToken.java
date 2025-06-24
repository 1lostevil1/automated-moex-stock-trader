package org.example.command.Action;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import org.example.command.Command;
import org.example.postgres.entity.State;
import org.example.postgres.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;


public class GetToken implements Command {
    @Override
    public List<BaseRequest<?, ? extends BaseResponse>> apply(Update update, UserRepository repository) {
        List<BaseRequest<?, ? extends BaseResponse>> actions = new ArrayList<>();
        Long id = update.message().chat().id();
        String message = update.message().text();
        try {
            ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
            if(!message.equals("Отмена")){
                repository.register(id,message);
                repository.setState(id, State.NONE);
                actions.add(new DeleteMessage(id,update.message().messageId()));
                actions.add(new SendMessage(id, "Вы зарегистрировались").replyMarkup(remove));
                return actions;
            }
            else{
                repository.setState(id, State.NONE);
                return List.of(new SendMessage(id, "Вы вернулись к меню").replyMarkup(remove));
            }
        } catch (Exception e) {
            return List.of(new SendMessage(id, "Токен не валидный попробуйте снова"));
        }
    }
}
