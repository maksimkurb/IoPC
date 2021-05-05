package ru.cubly.iopc.module.sysinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriveInfo {
    private String path;
    private Long totalSpace;
    private Long freeSpace;
    private Long usableSpace;
}
