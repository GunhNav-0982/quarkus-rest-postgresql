package vn.hung.itc.dto;

import lombok.Data;

import java.util.List;

@Data
public class DepartmentDto {
    private Long id;
    private String departmentName;
    private List<UserDto> userDto;
}
