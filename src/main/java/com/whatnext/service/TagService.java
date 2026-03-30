package com.whatnext.service;

import com.whatnext.dto.response.TagResponse;
import com.whatnext.exception.ResourceNotFoundException;
import com.whatnext.model.Tag;
import com.whatnext.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<TagResponse> getUserTags(Long userId) {
        return tagRepository.findByUserId(userId).stream()
                .map(TagResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TagResponse createTag(Long userId, String name, String color) {
        if (tagRepository.existsByUserIdAndName(userId, name)) {
            throw new RuntimeException("Tag already exists");
        }

        Tag tag = new Tag();
        tag.setUserId(userId);
        tag.setName(name);
        tag.setColor(color != null ? color : "#3B82F6");

        Tag savedTag = tagRepository.save(tag);
        return TagResponse.fromEntity(savedTag);
    }

    @Transactional
    public void deleteTag(Long userId, Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));

        if (!tag.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Tag", "id", tagId);
        }

        tagRepository.delete(tag);
    }

    @Transactional
    public Set<Tag> getOrCreateTags(Long userId, Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();

        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByUserIdAndName(userId, tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setUserId(userId);
                        newTag.setName(tagName);
                        newTag.setColor("#3B82F6");
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }

        return tags;
    }
}
