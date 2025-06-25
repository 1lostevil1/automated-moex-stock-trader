package org.example.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import org.example.postgres.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Start implements Command {
    @Override
    public SendMessage apply(Update update, UserRepository repository) {
        Long id = update.message().chat().id();
        String userName = update.message().chat().username();
        try {
            var entity = repository.getByTelegramName(userName);
            entity.setTelegramId(id);
            repository.update(entity);
            return new SendMessage(id, "Добро пожаловать, " + entity.getUsername());
        } catch (Exception e) {
            return new SendMessage(id,"Сначала зарегистрируйтесь на сайте");
        }
    }
}