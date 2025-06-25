package org.example.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.AllArgsConstructor;
import org.example.postgres.entity.State;
import org.example.postgres.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class UnSubscribe implements Command {
    @Override
    public List<BaseRequest<?, ? extends BaseResponse>> apply(Update update, UserRepository repository) {
        Long id = update.message().chat().id();
        try {
            repository.setState(id, State.UNSUBSCRIBE_TICKER);
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup("Отмена");
            keyboardMarkup.resizeKeyboard(true);
            return List.of(new SendMessage(id,"Для отписки от акции введите тикер").replyMarkup(keyboardMarkup));
        } catch (Exception e) {
            return List.of(new SendMessage(id, e.getMessage()));
        }
    }
}