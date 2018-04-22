package pt.isel.ps.gis.dal.repositories;

import pt.isel.ps.gis.model.UserList;
import pt.isel.ps.gis.model.UserListId;

public interface UserListRepositoryCustom {

    UserList insertUserList(UserList userList);

    void deleteUserListById(UserListId id); //TODO: rename
}
