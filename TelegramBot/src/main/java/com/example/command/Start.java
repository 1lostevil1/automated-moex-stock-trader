package com.example.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import org.example.postgres.entity.UserEntity;
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
            repository.save(new UserEntity(id,userName));
            return new SendMessage(id, "Добро пожаловать, " + userName);
        } catch (Exception e) {
            return new SendMessage(id, e.getMessage());
        }
    }
}