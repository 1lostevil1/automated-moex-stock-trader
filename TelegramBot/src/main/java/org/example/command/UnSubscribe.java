package org.example.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import org.example.postgres.entity.State;
import org.example.postgres.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UnSubscribe implements Command {
    @Override
    public SendMessage apply(Update update, UserRepository repository) {
        Long id = update.message().chat().id();
        try {
            repository.setState(id, State.UNSUBSCRIBE_TICKER);
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup("Отмена");
            keyboardMarkup.resizeKeyboard(true);
            return new SendMessage(id,"Для отписки от акции введите тикер").replyMarkup(keyboardMarkup);
        } catch (Exception e) {
            return new SendMessage(id, e.getMessage());
        }
    }
}