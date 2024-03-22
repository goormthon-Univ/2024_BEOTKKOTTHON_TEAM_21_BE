package com.teamkrews.chat.controller;

import com.teamkrews.User.model.User;
import com.teamkrews.auth.controller.AuthenticationPrincipal;
import com.teamkrews.chat.model.ChatRoom;
import com.teamkrews.chat.model.ChatRoomCreationDto;
import com.teamkrews.chat.model.request.ChatRoomCreationRequest;
import com.teamkrews.chat.model.response.ChatRoomResponse;
import com.teamkrews.chat.service.ChatRoomService;
import com.teamkrews.utill.ApiResponse;
import com.teamkrews.workspace.model.request.WorkspaceUUIDRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/chatRoom")
@Slf4j
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ModelMapper mapper;

    // 1:1 채팅방 생성 - 내가 피드백 보낸 경우
    // @RequestBody는 하나의 메서드에 여러 개일 수 없음 -> 따라서, 두 개의 Long 타입 인자로 받으려면 하나의 DTO 안에 넣어줘야 함!
    @PostMapping("")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createChatRoom(@AuthenticationPrincipal User user,
                                                                @RequestBody ChatRoomCreationRequest request) {
        ChatRoomCreationDto dto = mapper.map(request, ChatRoomCreationDto.class);
        dto.setCreatorUserId(user.getId());

        ChatRoomResponse response = chatRoomService.createChatRoomWithUser(dto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 전체 채팅방 목록 조회
    // 나중에 내가 먼저 보낸 채팅방 & 받은 채팅방으로 분리하기
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getChatRooms(@AuthenticationPrincipal User user, @RequestParam WorkspaceUUIDRequest workspaceRequest) {

        Long userId = user.getId();
        String workspaceUUID = workspaceRequest.getWorkspaceUUID();

        List<ChatRoomResponse> chatRoomResponses = chatRoomService.getChatRoomsByUserIdAndWorkspaceUUID(userId, workspaceUUID);

        return ResponseEntity.ok(ApiResponse.success(chatRoomResponses));
    }

    // 내가 보낸 채팅방 목록 조회
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getChatRoomsOfSent(@AuthenticationPrincipal User user, @RequestParam WorkspaceUUIDRequest workspaceRequest) {

        Long userId = user.getId();
        String workspaceUUID = workspaceRequest.getWorkspaceUUID();

        List<ChatRoomResponse> chatRoomResponses = chatRoomService.getChatRoomsOfSent(userId, workspaceUUID);

        return ResponseEntity.ok(ApiResponse.success(chatRoomResponses));
    }

    // 내가 받은 채팅방 목록 조회
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getChatRoomsOfReceived(@AuthenticationPrincipal User user, @RequestParam WorkspaceUUIDRequest workspaceRequest) {

        Long userId = user.getId();
        String workspaceUUID = workspaceRequest.getWorkspaceUUID();

        List<ChatRoomResponse> chatRoomResponses = chatRoomService.getChatRoomsOfReceived(userId, workspaceUUID);

        return ResponseEntity.ok(ApiResponse.success(chatRoomResponses));
    }
}