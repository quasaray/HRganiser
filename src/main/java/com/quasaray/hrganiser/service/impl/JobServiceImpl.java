package com.quasaray.hrganiser.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.quasaray.hrganiser.domain.Job;
import com.quasaray.hrganiser.repository.JobRepository;
import com.quasaray.hrganiser.repository.search.JobSearchRepository;
import com.quasaray.hrganiser.service.JobService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Job}.
 */
@Service
@Transactional
public class JobServiceImpl implements JobService {

    private final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    private final JobRepository jobRepository;

    private final JobSearchRepository jobSearchRepository;

    public JobServiceImpl(JobRepository jobRepository, JobSearchRepository jobSearchRepository) {
        this.jobRepository = jobRepository;
        this.jobSearchRepository = jobSearchRepository;
    }

    @Override
    public Job save(Job job) {
        log.debug("Request to save Job : {}", job);
        Job result = jobRepository.save(job);
        jobSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Job> partialUpdate(Job job) {
        log.debug("Request to partially update Job : {}", job);

        return jobRepository
            .findById(job.getId())
            .map(existingJob -> {
                if (job.getJobTitle() != null) {
                    existingJob.setJobTitle(job.getJobTitle());
                }

                return existingJob;
            })
            .map(jobRepository::save)
            .map(savedJob -> {
                jobSearchRepository.save(savedJob);

                return savedJob;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Job> findAll(Pageable pageable) {
        log.debug("Request to get all Jobs");
        return jobRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Job> findOne(Long id) {
        log.debug("Request to get Job : {}", id);
        return jobRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Job : {}", id);
        jobRepository.deleteById(id);
        jobSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Job> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Jobs for query {}", query);
        return jobSearchRepository.search(query, pageable);
    }
}
