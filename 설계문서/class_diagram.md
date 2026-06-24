classDiagram
      direction TB

      %% ===== USER DOMAIN =====
      class User {
          +Long userId
          +String email
          +String passwordHash
          +String nickname
          +String profileImageUrl
          +String provider
          +String providerId
          +Long selectedCoachId
          +String status
          +Boolean onboardingCompleted
          +LocalDateTime createdAt
          +LocalDateTime updatedAt
      }

      class HealthProfile {
          +Long healthProfileId
          +Long userId
          +BigDecimal heightCm
          +BigDecimal weightKg
          +BigDecimal targetWeightKg
          +LocalDate birthDate
          +String gender
          +String healthGoal
          +String activityLevel
          +BigDecimal targetCalories
          +BigDecimal targetProteinG
          +BigDecimal targetCarbsG
          +BigDecimal targetFatG
          +LocalDateTime createdAt
          +LocalDateTime updatedAt
      }

      class Coach {
          +Long coachId
          +String name
          +String role
          +String toneDescription
          +String sampleMessage
          +Boolean active
          +LocalDateTime createdAt
      }

      %% ===== CHARACTER DOMAIN =====
      class CharacterEntity {
          +Long characterId
          +Long userId
          +String name
          +Integer level
          +Integer xp
          +String stage
          +String mood
          +String moodMessage
          +String appearanceType
          +Integer streakDays
          +Integer bestStreakDays
          +LocalDateTime createdAt
          +LocalDateTime updatedAt
      }

      %% ===== DIET DOMAIN =====
      class Diet {
          +Long dietId
          +Long userId
          +MealType mealType
          +LocalDateTime eatenAt
          +String memo
          +BigDecimal totalCalories
          +BigDecimal totalProteinG
          +BigDecimal totalCarbsG
          +BigDecimal totalFatG
          +LocalDateTime createdAt
          +LocalDateTime updatedAt
      }

      class DietItem {
          +Long dietItemId
          +Long dietId
          +Long foodId
          +BigDecimal inputAmount
          +String inputUnit
          +BigDecimal amountG
          +BigDecimal amountMl
          +BigDecimal calories
          +BigDecimal proteinG
      }

      class Food {
          +Long foodId
          +String externalFoodCode
          +String name
          +String brand
          +String category
          +BigDecimal calories
          +BigDecimal proteinG
          +BigDecimal carbsG
          +BigDecimal fatG
          +String source
          +LocalDateTime createdAt
      }

      %% ===== GUILD DOMAIN =====
      class Guild {
          +Long guildId
          +String name
          +String description
          +String inviteCode
          +Long ownerUserId
          +Integer maxMembers
          +Integer guildPoint
          +String visibility
          +String status
          +LocalDateTime createdAt
          +LocalDateTime updatedAt
      }

      class GuildMember {
          +Long guildMemberId
          +Long guildId
          +Long userId
          +String role
          +LocalDateTime joinedAt
          +LocalDateTime leftAt
      }

      %% ===== BOSS DOMAIN =====
      class BossBattle {
          +Long battleId
          +Long guildId
          +Long bossId
          +Long seasonId
          +String status
          +Integer maxHp
          +Integer currentHp
          +Integer totalDamage
          +LocalDateTime startedAt
          +LocalDateTime endedAt
      }

      class BossBattleParticipant {
          +Long participantId
          +Long battleId
          +Long guildId
          +Long userId
          +Long guildMemberId
          +String roleAtStart
          +String status
          +String snapshotNickname
          +Integer snapshotCharacterLevel
          +LocalDateTime joinedAt
      }

      %% ===== QUEST DOMAIN =====
      class QuestTemplate {
          +Long templateId
          +String title
          +String description
          +String questType
          +String conditionCategory
          +String metricType
          +Integer damage
          +Integer rewardExp
          +Integer rewardCoin
          +String difficulty
          +Boolean active
      }

      class Quest {
          +Long questId
          +Long battleId
          +Long guildId
          +Long userId
          +Long questTemplateId
          +String title
          +String questType
          +Integer targetValue
          +Integer currentValue
          +Integer damage
          +Integer rewardExp
          +Integer rewardCoin
          +String status
          +LocalDateTime createdAt
          +LocalDateTime completedAt
      }

      %% ===== ITEM DOMAIN =====
      class Item {
          +Long itemId
          +String name
          +String description
          +String itemType
          +String slotType
          +Integer price
          +String imageUrl
          +Boolean defaultItem
          +Boolean purchasable
          +LocalDateTime createdAt
      }

      class UserItem {
          +Long userItemId
          +Long userId
          +Long itemId
          +String acquiredType
          +Long acquiredSourceId
          +LocalDateTime acquiredAt
      }

      %% ===== COIN DOMAIN =====
      class CoinBalance {
          +Long userId
          +Integer balance
          +LocalDateTime createdAt
          +LocalDateTime updatedAt
      }

      class CoinTransaction {
          +Long transactionId
          +Long userId
          +String transactionType
          +Integer amount
          +Integer balanceAfter
          +String sourceType
          +Long sourceId
          +String description
          +LocalDateTime createdAt
      }

      %% ===== AI DOMAIN =====
      class AiReport {
          +Long reportId
          +Long userId
          +String reportType
          +LocalDate periodStart
          +LocalDate periodEnd
          +String summary
          +Integer healthScore
          +LocalDateTime createdAt
      }

      %% ===== RELATIONSHIPS =====
      User "1" --> "1" HealthProfile : has
      User "N" --> "1" Coach : selects
      User "1" --> "1" CharacterEntity : owns
      User "1" --> "N" Diet : records
      User "1" --> "1" CoinBalance : has
      User "1" --> "N" CoinTransaction : generates
      User "1" --> "N" UserItem : owns
      User "1" --> "N" AiReport : receives

      Diet "1" --> "N" DietItem : contains
      DietItem "N" --> "1" Food : references

      Guild "1" --> "N" GuildMember : has
      GuildMember "N" --> "1" User : is

      Guild "1" --> "N" BossBattle : hosts
      BossBattle "1" --> "N" BossBattleParticipant : has
      BossBattleParticipant "N" --> "1" User : is

      BossBattle "1" --> "N" Quest : generates
      Quest "N" --> "1" QuestTemplate : based on
      Quest "N" --> "1" User : assigned to

      UserItem "N" --> "1" Item : references