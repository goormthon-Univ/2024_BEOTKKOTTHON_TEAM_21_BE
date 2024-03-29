package com.teamkrews.message.service;

import com.teamkrews.User.model.User;
import com.teamkrews.User.model.UserInfo;
import com.teamkrews.User.service.UserService;
import com.teamkrews.chatroom.model.ChatRoom;
import com.teamkrews.message.model.Message;
import com.teamkrews.message.model.request.MessageDTO;
import com.teamkrews.message.model.response.MessageResponse;
import com.teamkrews.message.model.response.MessageResponses;
import com.teamkrews.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MessageService {
    private final UserService userService;
    private final MessageRepository messageRepository;
    private final ModelMapper mapper;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분");


    // 특정 유저의 특정 워크스페이스의 특정 채팅방의 메시지 조회
    public List<Message> getMessages(ChatRoom chatRoom) {
        List<Message> messageList = messageRepository.findAllByChatRoom(chatRoom);
        return messageList;
    }

    // 메시지 저장
    public Message saveMessage(ChatRoom chatRoom, MessageDTO messageDTO) {
        Message message = mapper.map(messageDTO, Message.class);
        message.setChatRoom(chatRoom);

        return messageRepository.save(message);
    }

    public MessageResponse convertMessageResponse(Message message, UserInfo userInfo){
        if (message == null)
            return null;

        MessageResponse messageResponse = mapper.map(message, MessageResponse.class);
        messageResponse.setDateTime(message.getCreatedAt().format(formatter));
        messageResponse.setUserInfo(userInfo);
        return messageResponse;
    }

    public MessageResponses convertMessageResponses(List<Message> messages){
        if (messages == null)
            return null;

        List<MessageResponse> messageResponseList = messages.stream().map(
                (message) -> {
                    User user = userService.getById(message.getSenderId());
                    return convertMessageResponse(message, userService.convertToInfo(user));
                }
        ).collect(Collectors.toList());

        return new MessageResponses(messageResponseList);
    }
}
