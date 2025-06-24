package org.example.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.postgres.entity.State;
import org.example.postgres.repository.UserRepository;

public class Register implements Command{
    @Override
    public SendMessage apply(Update update, UserRepository repository) {
        Long id = update.message().chat().id();
        try {
            repository.setState(id, State.WAIT_TOKEN);
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup("Отмена");
            keyboardMarkup.resizeKeyboard(true);
            return new SendMessage(id,"Для регистрации введите токен Т-инвестиций").replyMarkup(keyboardMarkup);
        } catch (Exception e) {
            return new SendMessage(id, e.getMessage());
        }
    }
}
