package uet.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseDAO <T> {
    public abstract String getTableName();
    public abstract T mapRow(ResultSet rs) throws SQLException;


}
