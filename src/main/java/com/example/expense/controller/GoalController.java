package com.example.expense.controller;

import com.example.expense.dto.GoalRequest;
import com.example.expense.dto.GoalResponse;
import com.example.expense.entity.Goal;
import com.example.expense.entity.User;
import com.example.expense.exception.AccessDeniedException;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.service.GoalService;
import com.example.expense.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

  private final GoalService goalService;
  private final UserService userService;

  private GoalResponse mapToResponse(Goal g) {
    return new GoalResponse(
        g.getId(),
        g.getUuid(),
        g.getName(),
        g.getGoalType(),
        g.getTargetAmount(),
        g.getCurrentAmount(),
        g.getPeriod(),
        g.getDeadline(),
        g.getAchieved());
  }

  private Goal mapToEntity(GoalRequest req, User user, Long id, String uuid) {
    Goal g = new Goal();
    if (id != null) g.setId(id);
    if (uuid != null) g.setUuid(uuid);

    g.setUser(user);
    g.setName(req.getName());
    g.setGoalType(req.getGoalType());
    g.setTargetAmount(req.getTargetAmount());
    g.setPeriod(req.getPeriod());
    g.setDeadline(req.getDeadline());
    return g;
  }

  @GetMapping
  public List<GoalResponse> list(Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    return goalService.findByUserId(user.getId()).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @PostMapping
  public GoalResponse create(@RequestBody @Valid GoalRequest request, Authentication auth) {
    User user = userService.getByEmail(auth.getName());

    Goal goal = mapToEntity(request, user, null, null);

    Goal saved = goalService.create(goal);

    return mapToResponse(saved);
  }

  @PutMapping("/{id}")
  public GoalResponse update(
      @PathVariable Long id, @RequestBody @Valid GoalRequest request, Authentication auth) {
    User user = userService.getByEmail(auth.getName());

    Goal existing =
        goalService
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));

    if (!existing.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("not_allowed");
    }

    Goal updatedEntity = mapToEntity(request, user, id, existing.getUuid());
    updatedEntity.setCurrentAmount(existing.getCurrentAmount());
    updatedEntity.setAchieved(existing.getAchieved());

    Goal updated = goalService.update(updatedEntity);

    return mapToResponse(updated);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id, Authentication auth) {
    User user = userService.getByEmail(auth.getName());

    Goal existing =
        goalService
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));

    if (!existing.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("not_allowed");
    }

    goalService.delete(id);
  }
}
