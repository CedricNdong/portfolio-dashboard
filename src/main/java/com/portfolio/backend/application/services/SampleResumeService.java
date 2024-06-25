package com.portfolio.backend.application.services;

import com.portfolio.backend.application.data.SampleResume;
import com.portfolio.backend.application.data.SampleResumeRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SampleResumeService {

    private final SampleResumeRepository repository;

    public SampleResumeService(SampleResumeRepository repository) {
        this.repository = repository;
    }

    public Optional<SampleResume> get(Long id) {
        return repository.findById(id);
    }

    public SampleResume update(SampleResume entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<SampleResume> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<SampleResume> list(Pageable pageable, Specification<SampleResume> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
