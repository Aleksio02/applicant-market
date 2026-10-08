package com.maboy.applicantmarket.assignment.service;

import com.maboy.applicantmarket.assignment.api.AssignmentModuleApi;
import com.maboy.applicantmarket.assignment.dao.AssignmentAttemptDao;
import com.maboy.applicantmarket.assignment.dao.AssignmentDao;
import com.maboy.applicantmarket.assignment.dao.dto.AssignmentAttemptDto;
import com.maboy.applicantmarket.assignment.dao.dto.VacancyAssignmentDto;
import com.maboy.applicantmarket.assignment.model.enums.EvaluationVerdict;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentModuleApiImpl implements AssignmentModuleApi {

    private final AssignmentDao assignmentDao;
    private final AssignmentAttemptDao attemptDao;

    @Override
    public boolean hasPassedAssignment(UUID candidateId, UUID vacancyId) {
        if (candidateId == null || vacancyId == null) {
            return false;
        }
        List<AssignmentAttemptDto> passed = attemptDao
                .findAllByAssignmentVacancyIdAndVerdict(vacancyId, EvaluationVerdict.PASS);
        return passed.stream().anyMatch(a -> a.getCandidateId().equals(candidateId));
    }

    @Override
    public Optional<UUID> findAssignmentIdByVacancy(UUID vacancyId) {
        if (vacancyId == null) {
            return Optional.empty();
        }
        return assignmentDao.findByVacancyId(vacancyId)
                .map(VacancyAssignmentDto::getId);
    }
}