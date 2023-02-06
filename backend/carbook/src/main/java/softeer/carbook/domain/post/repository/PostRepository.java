package softeer.carbook.domain.post.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import softeer.carbook.domain.post.model.Post;
import softeer.carbook.domain.user.model.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PostRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostRepository(DataSource dataSource) { this.jdbcTemplate = new JdbcTemplate(dataSource); }

    public List<Post> getPosts(int size, int index){
        List<Post> posts = jdbcTemplate.query("select * from POST where  ORDER BY create_date DESC LIMIT ?, ?",
                postRowMapper(), index, size);
        return posts;
    }

    private RowMapper<Post> postRowMapper(){
        return (rs, rowNum) -> {
            Post post = new Post(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getTimestamp("create_date"),
                    rs.getTimestamp("update_date"),
                    rs.getString("content")
            );
            return post;
        };
    }

}
