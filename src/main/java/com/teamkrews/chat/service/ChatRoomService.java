package com.teamkrews.chat.service;

import com.teamkrews.User.service.UserService;
import com.teamkrews.chat.model.ChatRoom;
import com.teamkrews.chat.model.ChatRoomUser;
import com.teamkrews.User.model.User;
import com.teamkrews.chat.model.request.ChatRoomCreationRequest;
import com.teamkrews.chat.model.response.ChatRoomResponse;
import com.teamkrews.chat.repository.ChatRoomRepository;
import com.teamkrews.chat.repository.ChatRoomUserRepository;
import com.teamkrews.global.exception.CustomException;
import com.teamkrews.global.exception.ErrorCode;
import com.teamkrews.workspace.model.Workspace;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.teamkrews.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatRoomService {

    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final WorkspaceService workspaceService;

    // 1:1 채팅방 생성
    @Transactional
    public ChatRoom createChatRoomWithUser(ChatRoomCreationRequest request) {

        User currentUser = userService.getById(request.getCurrentUserId());
        User targetUser = userService.getById(request.getTargetUserId());
        Workspace workspace = workspaceService.findByUUID(request.getWorkspaceUUID());

        // 채팅방 생성
        ChatRoom chatRoom = new ChatRoom();
        chatRoomRepository.save(chatRoom);

        createAndSaveChatRoomUser(chatRoom, currentUser, workspace, 1);
        createAndSaveChatRoomUser(chatRoom, targetUser, workspace, 0);

        return chatRoom;
    }

    private void createAndSaveChatRoomUser(ChatRoom chatRoom, User user, Workspace workspace, int isCreator) {
        ChatRoomUser chatRoomUser = new ChatRoomUser();
        chatRoomUser.setChatRoom(chatRoom);
        chatRoomUser.setUser(user);
        chatRoomUser.setWorkspace(workspace);
        chatRoomUser.setIsCreator(isCreator);
        chatRoomUserRepository.save(chatRoomUser);
    }

    // 채팅방 조회
    public List<ChatRoomResponse> getChatRoomsByUserIdAndWorkspaceUUID(Long userId, String workspaceUUID) {

        User user = userService.getById(userId);
        Workspace workspace = workspaceService.findByUUID(workspaceUUID);

        // chatRoomRepository를 사용하여 채팅방 목록 조회
        List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findByUserAndWorkspace(user, workspace);

        // 조회된 채팅방 목록을 ChatRoomResponse 리스트로 변환
        return chatRoomUsers.stream()
                .map(chatRoomUser -> {
                    ChatRoomResponse response = new ChatRoomResponse();
                    response.setChatRoomId(chatRoomUser.getChatRoom().getChatRoomId());
                    response.setWorkspaceUUID(chatRoomUser.getWorkspace().getWorkspaceUUID());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public ChatRoom findById(final Long chatRoomId){
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoomId);

        if(chatRoomOptional.isEmpty()){
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        return chatRoomOptional.get();
    }

    // 내가 보낸 채팅방 조회
    public List<ChatRoomResponse> getChatRoomsOfSent(Long userId, String workspaceUUID) {
        User user = userService.getById(userId);
        Workspace workspace = workspaceService.findByUUID(workspaceUUID);

        List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findByUserAndWorkspaceAndIsCreator(user, workspace, 1);

        return chatRoomUsers.stream()
                .map(chatRoomUser -> {
                    ChatRoomResponse response = new ChatRoomResponse();
                    response.setChatRoomId(chatRoomUser.getChatRoom().getChatRoomId());
                    response.setWorkspaceUUID(chatRoomUser.getWorkspace().getWorkspaceUUID());
                    return response;
                })
                .collect(Collectors.toList());
    }

    // 내가 받은 채팅방 조회
    public List<ChatRoomResponse> getChatRoomsOfReceived(Long userId, String workspaceUUID) {
        User user = userService.getById(userId);
        Workspace workspace = workspaceService.findByUUID(workspaceUUID);

        List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findByUserAndWorkspaceAndIsCreator(user, workspace, 0);

        return chatRoomUsers.stream()
                .map(chatRoomUser -> {
                    ChatRoomResponse response = new ChatRoomResponse();
                    response.setChatRoomId(chatRoomUser.getChatRoom().getChatRoomId());
                    response.setWorkspaceUUID(chatRoomUser.getWorkspace().getWorkspaceUUID());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
