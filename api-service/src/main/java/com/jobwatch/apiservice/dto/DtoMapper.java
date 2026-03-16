package com.jobwatch.apiservice.dto;

import com.jobwatch.apiservice.models.User;
import com.jobwatch.apiservice.models.Watchlist;

public class DtoMapper {

    // Convert User entity to UserDTO (no password)
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    // Convert Watchlist entity to WatchlistDTO
    public static WatchlistDTO toWatchlistDTO(Watchlist watchlist) {
        return new WatchlistDTO(
                watchlist.getId(),
                toUserDTO(watchlist.getUser()),
                watchlist.getCompany(),
                watchlist.getCreatedAt()
        );
    }
}