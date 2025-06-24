package org.example.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.AllArgsConstructor;
import org.example.postgres.entity.UserEntity;
import org.example.postgres.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class Start implements Command {
    @Override
    public List<BaseRequest<?, ? extends BaseResponse>> apply(Update update, UserRepository repository) {
        Long id = update.message().chat().id();
        String userName = update.message().chat().username();
        try {
            repository.save(new UserEntity(id,userName));
            return List.of(new SendMessage(id, "Добро пожаловать, " + userName));
        } catch (Exception e) {
            return List.of(new SendMessage(id, e.getMessage()));
        }
    }
}