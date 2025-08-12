package locser.toy.domain.model.event;

import java.time.LocalDateTime;

/**
 * Domain event that is published when a participation is created.
 * This follows DDD principles by keeping domain events in the domain layer.
 */
public class ParticipationCreatedEvent {
    
    private final Long toyId;
    private final Long userId;
    private final Long campaignId;
    private final Integer participationStatus;
    private final LocalDateTime occurredOn;
    
    public ParticipationCreatedEvent(Long toyId, Long userId, Long campaignId, Integer participationStatus) {
        this.toyId = toyId;
        this.userId = userId;
        this.campaignId = campaignId;
        this.participationStatus = participationStatus;
        this.occurredOn = LocalDateTime.now();
    }
    
    public Long getToyId() {
        return toyId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Long getCampaignId() {
        return campaignId;
    }
    
    public Integer getParticipationStatus() {
        return participationStatus;
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String toString() {
        return "ParticipationCreatedEvent{" +
                "toyId=" + toyId +
                ", userId=" + userId +
                ", campaignId=" + campaignId +
                ", participationStatus=" + participationStatus +
                ", occurredOn=" + occurredOn +
                '}';
    }
}