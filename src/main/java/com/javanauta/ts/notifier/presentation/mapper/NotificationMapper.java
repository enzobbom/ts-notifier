package com.javanauta.ts.notifier.presentation.mapper;


import com.javanauta.ts.notifier.presentation.dto.NotifyTaskRequestDTO;
import com.javanauta.ts.notifier.service.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneId;

@Mapper(componentModel = "spring", imports = { java.time.ZoneId.class, NotificationMapper.class })
public interface NotificationMapper {

    @Mapping(target = "title", source = "name")
    @Mapping(target = "recipient", source = "userEmail")
    @Mapping(target = "status", source = "notificationStatus")
    @Mapping(target = "timeZoneId", expression = "java(NotificationMapper.convertTimeZoneId(dto))")
    Notification toDomain(NotifyTaskRequestDTO dto);

    static ZoneId convertTimeZoneId(NotifyTaskRequestDTO dto) {
        return ZoneId.of(dto.timeZoneId());
    }
}

