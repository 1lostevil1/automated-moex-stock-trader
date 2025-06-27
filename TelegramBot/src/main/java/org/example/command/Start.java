package org.example.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import org.example.postgres.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Start implements Command {
    private static final String OK_MESSAGE = "Добро пожаловать, ";
    private static final String ERROR_MESSAGE = "Сначала зарегистрируйтесь на сайте";
    @Override
    public SendMessage apply(Update update, UserRepository repository) {
        Long id = update.message().chat().id();
        String userName = update.message().chat().username();
        try {
            var entity = repository.getByTelegramUsername(userName);
            entity.setTelegramId(id);
            repository.update(entity);
            return new SendMessage(id,  OK_MESSAGE + entity.getUsername());
        } catch (Exception e) {
            return new SendMessage(id, ERROR_MESSAGE);
        }
    }
}