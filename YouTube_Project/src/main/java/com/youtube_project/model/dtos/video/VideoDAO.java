package com.youtube_project.model.dtos.video;

import com.youtube_project.model.dtos.user.UserResponseWithSubscribersAndVideosCountDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class VideoDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String findCountOfUserUploads = "SELECT u.id, u.first_name, COUNT(v.id) AS uploads FROM users as u LEFT JOIN videos as v on u.id = v.owner_id GROUP by u.id ;";

    private String queryUserIdAndNumberObservers = "SELECT subscribed_to, COUNT(subscriber_id) AS subscribers FROM subscribers GROUP BY subscribed_to";


    public List<VideoSimpleResponseDTO> getVideosByMostWatched(int rows, int pageNumber) {

        String query =
                "SELECT v.id,v.owner_id AS owner,v.title,v.video_url,v.date_of_upload, u.first_name,u.last_name,u.profile_photo,number_of_likes,views FROM videos AS v LEFT join (select video_id,count(*) AS number_of_likes FROM videos_have_reactions WHERE reaction LIKE 'l' GROUP BY video_id)\n" +
                        "AS likes ON likes.video_id = v.id\n" +
                        "LEFT JOIN users AS u ON u.id = v.owner_id\n" +
                        "LEFT JOIN (select video_id,COUNT(*) AS views FROM users_watched_videos GROUP BY video_id ) AS views ON v.id = views.video_id WHERE v.is_private = 0 AND u.is_deleted = 0 GROUP BY  v.id ORDER BY views DESC LIMIT " + pageNumber + ", " + (rows + (pageNumber * 2));


        return getVideoSimpleResponseDTOS(query, findCountOfUserUploads, queryUserIdAndNumberObservers);

    }

    public List<VideoSimpleResponseDTO> getMostLikedVideos(int rows, int pageNumber) {

        String query = "SELECT v.id,v.owner_id AS owner,v.title,v.video_url,v.date_of_upload, u.first_name,u.last_name,u.profile_photo,number_of_likes,views FROM videos AS v LEFT join (select video_id,count(*) AS number_of_likes FROM videos_have_reactions WHERE reaction LIKE 'l' GROUP BY video_id)\n" +
                "AS likes ON likes.video_id = v.id\n" +
                "LEFT JOIN users AS u ON u.id = v.owner_id\n" +
                "LEFT JOIN (select video_id,COUNT(*) AS views FROM users_watched_videos GROUP by video_id ) AS views ON v.id = views.video_id WHERE v.is_private = 0 AND u.is_deleted = 0 AND  CURRENT_DATE() > CURRENT_DATE() - INTERVAL 7 DAY GROUP BY  v.id ORDER BY number_of_likes DESC LIMIT " + pageNumber + ", " + (rows + (pageNumber * 2));

        return getVideoSimpleResponseDTOS(query, findCountOfUserUploads, queryUserIdAndNumberObservers);

    }

    private List<VideoSimpleResponseDTO> getVideoSimpleResponseDTOS(String query, String findCountOfUserUploads, String queryUserIdAndNumberObservers) {
        List<VideoSimpleResponseDTO> videoSimpleResponseDTOS = jdbcTemplate.query(query, resultSet -> {
            List<VideoSimpleResponseDTO> simpleVideos = new ArrayList<>();

            while (resultSet.next()) {
                simpleVideos.add(buildSimpleVideo(resultSet));
            }
            return simpleVideos;
        });

        Map<Long, Long> uploadsVideos = new HashMap<>();
        jdbcTemplate.query(findCountOfUserUploads, resultSet -> {
            while (resultSet.next()) {
                uploadsVideos.put(resultSet.getLong("id"), resultSet.getLong("uploads"));
            }
        });

        Map<Long, Long> followersUsers = new HashMap<>();
        jdbcTemplate.query(queryUserIdAndNumberObservers, resultSet -> {
            while (resultSet.next()) {
                followersUsers.put(resultSet.getLong("subscribed_to"), resultSet.getLong("subscribers"));
            }
        });

        for (VideoSimpleResponseDTO videoSimpleResponseDTO : videoSimpleResponseDTOS) {
            long userId = videoSimpleResponseDTO.getUser().getId();
            videoSimpleResponseDTO.getUser().setVideos(uploadsVideos.get(userId) == null ? 0 : uploadsVideos.get(userId));
            videoSimpleResponseDTO.getUser().setFollowers(followersUsers.get(userId) == null ? 0 : followersUsers.get(userId));
        }

        return videoSimpleResponseDTOS;
    }


    private VideoSimpleResponseDTO buildSimpleVideo(ResultSet resultSet) throws SQLException {
        VideoSimpleResponseDTO videoSimpleResponseDTO = new VideoSimpleResponseDTO();
        videoSimpleResponseDTO.setId(resultSet.getInt("id"));
        videoSimpleResponseDTO.setUser(buildSimpleUser(resultSet));
        videoSimpleResponseDTO.setTitle(resultSet.getString("title"));
        videoSimpleResponseDTO.setVideoUrl(resultSet.getString("video_url"));
        videoSimpleResponseDTO.setUploadDate(resultSet.getDate("date_of_upload").toLocalDate());
        videoSimpleResponseDTO.setViews(resultSet.getInt("views"));
        videoSimpleResponseDTO.setLikes(resultSet.getLong("number_of_likes"));
        return videoSimpleResponseDTO;
    }

    private UserResponseWithSubscribersAndVideosCountDTO buildSimpleUser(ResultSet resultSet) throws SQLException {

        UserResponseWithSubscribersAndVideosCountDTO dto = new UserResponseWithSubscribersAndVideosCountDTO();
        dto.setId(resultSet.getInt("owner"));
        dto.setFirstName(resultSet.getString("first_name"));
        dto.setLastName(resultSet.getString("last_name"));

        return dto;
    }

}
