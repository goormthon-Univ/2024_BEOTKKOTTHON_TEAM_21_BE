package com.teamkrews.workspace.service;


import com.teamkrews.global.exception.CustomException;
import com.teamkrews.global.exception.ErrorCode;
import com.teamkrews.workspace.model.Workspace;
import com.teamkrews.workspace.model.WorkspaceCreateDto;
import com.teamkrews.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final ModelMapper mapper;
    private final WorkspaceRepository workspaceRepository;

    public Workspace create(final WorkspaceCreateDto dto){
        Workspace workspace = mapper.map(dto, Workspace.class);
        workspace.setWorkspaceUUID(UUID.randomUUID().toString());
        Workspace saved = workspaceRepository.save(workspace);

        return saved;
    }

    public Workspace findByUUID(final String workspaceUUID){
        Optional<Workspace> workspaceOptional = workspaceRepository.findByWorkspaceUUID(workspaceUUID);

        if (workspaceOptional.isEmpty()){
            throw new CustomException(ErrorCode.WORKSPACE_NOT_FOUND);
        }

        return workspaceOptional.get();
    }
}
