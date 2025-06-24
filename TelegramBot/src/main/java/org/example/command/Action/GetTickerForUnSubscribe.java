package org.example.command.Action;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.AllArgsConstructor;
import org.example.command.Command;
import org.example.postgres.entity.State;
import org.example.postgres.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class GetTickerForUnSubscribe implements Command {
    @Override
    public List<BaseRequest<?, ? extends BaseResponse>> apply(Update update, UserRepository repository) {
        Long id = update.message().chat().id();
        String message = update.message().text();
        try {
            ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
            if(!message.equals("Отмена")){
                repository.unsubscribe(id,message);
                repository.setState(id, State.NONE);
                return List.of(new SendMessage(id, "Вы отписались от тикера" + message).replyMarkup(remove));
            }
            else{
                repository.setState(id, State.NONE);
                return List.of(new SendMessage(id, "Вы вернулись к меню").replyMarkup(remove));
            }
        } catch (Exception e) {
            return List.of(new SendMessage(id, "Данный тикер не найден попробуйте еще раз"));
        }
    }
}
