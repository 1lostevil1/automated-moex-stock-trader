package org.example.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import org.example.postgres.entity.State;
import org.example.postgres.repository.UserRepository;

import java.util.List;

public class Register implements Command{
    @Override
    public List<BaseRequest<?, ? extends BaseResponse>> apply(Update update, UserRepository repository) {
        Long id = update.message().chat().id();
        try {
            repository.setState(id, State.WAIT_TOKEN);
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup("Отмена");
            keyboardMarkup.resizeKeyboard(true);
            return List.of(new SendMessage(id,"Для регистрации введите токен Т-инвестиций")
                    .replyMarkup(keyboardMarkup));
        } catch (Exception e) {
            return List.of(new SendMessage(id, e.getMessage()));
        }
    }
}
