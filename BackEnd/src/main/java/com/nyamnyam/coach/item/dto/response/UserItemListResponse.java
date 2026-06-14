package com.nyamnyam.coach.item.dto.response;

import java.util.List;

public record UserItemListResponse(
        List<UserItemResponse> items
) {
}
