package com.restapi.emp.service.impl;

import com.restapi.emp.dto.DepartmentDto;
import com.restapi.emp.dto.mapper.DepartmentMapper;
import com.restapi.emp.entity.Department;
import com.restapi.emp.exception.ResourceNotFoundException;
import com.restapi.emp.repository.DepartmentRepository;
import com.restapi.emp.service.DepartmentService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    // Constructor Injection 방식 / 생성자의 argument 로 의존할 레퍼런스를 받음
//    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
//        this.departmentRepository = departmentRepository;
//    }

    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        Department department = DepartmentMapper.mapToDepartment(departmentDto);
        Department savedDepartment = departmentRepository.save(department);
        return DepartmentMapper.mapToDepartmentDto(savedDepartment);
    }

    @Transactional(readOnly = true)
    @Override
    public DepartmentDto getDepartmentById(Long departmentId) {

        // optional <T> T가 null 일 수도 있고 아닐 수도 있다. null 이 아닌 경우에 T 반환, 맞으면 supplier 쪽에서 T를 return 한다.
        // T 값이 없으면 supplier 쪽에서 exception 반환
//        Optional<Department> optional = departmentRepository.findById(departmentId);
//        Department department = optional.orElseThrow(
//                () -> new ResourceNotFoundException("Department is not exists with a given id: " + departmentId));

        Department department = getDepartment(departmentId);
        return DepartmentMapper.mapToDepartmentDto(department);
    }

    private Department getDepartment(Long departmentId) {
        String errMsg = String.format("Department is not exists with a given id: %s", departmentId);
        return departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(errMsg, HttpStatus.NOT_FOUND)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<DepartmentDto> getAllDepartments() {
        // List<Department> ==> List<DepartmentDto>
        List<Department> departments = departmentRepository.findAll();
        return departments
                .stream()
                .map(DepartmentMapper::mapToDepartmentDto)
                .toList();  // Stream<DepartmentDto> => List<DepartmentDto>
                //.map((department) -> DepartmentMapper.mapToDepartmentDto(department))
                //.collect(Collectors.toList());
    }

    @Override
    public DepartmentDto updateDepartment(Long departmentId, DepartmentDto updatedDepartment) {
        Department department = getDepartment(departmentId);

        // Dirty check - setter method 만 호출 (@Transactional 사용)
        department.setDepartmentName(updatedDepartment.getDepartmentName());
        department.setDepartmentDescription(updatedDepartment.getDepartmentDescription());

         //Department savedDepartment = departmentRepository.save(department);

        return DepartmentMapper.mapToDepartmentDto(department);
    }

    @Override
    public void deleteDepartment(Long departmentId) {
        Department department = getDepartment(departmentId);

        departmentRepository.delete(department);
    }
}