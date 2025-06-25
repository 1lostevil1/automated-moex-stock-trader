package org.example.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import org.example.postgres.repository.UserRepository;

import java.util.List;

public interface Command {
    List<BaseRequest<?, ? extends BaseResponse>> apply(Update update, UserRepository repository);
}
