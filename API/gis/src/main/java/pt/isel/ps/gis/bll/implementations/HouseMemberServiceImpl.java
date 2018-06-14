package pt.isel.ps.gis.bll.implementations;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.ps.gis.bll.HouseMemberService;
import pt.isel.ps.gis.dal.repositories.HouseRepository;
import pt.isel.ps.gis.dal.repositories.UserHouseRepository;
import pt.isel.ps.gis.dal.repositories.UsersRepository;
import pt.isel.ps.gis.exceptions.EntityException;
import pt.isel.ps.gis.exceptions.EntityNotFoundException;
import pt.isel.ps.gis.model.UserHouse;
import pt.isel.ps.gis.model.Users;
import pt.isel.ps.gis.utils.ValidationsUtils;

import java.util.List;

@Service
public class HouseMemberServiceImpl implements HouseMemberService {

    private static final String HOUSE_NOT_EXIST = "House does not exist.";
    private static final String MEMBER_NOT_EXIST = "Member does not exist.";
    private static final String USER_NOT_EXIST = "User does not exist.";

    private final UserHouseRepository userHouseRepository;
    private final HouseRepository houseRepository;
    private final UsersRepository usersRepository;

    public HouseMemberServiceImpl(UserHouseRepository userHouseRepository, HouseRepository houseRepository, UsersRepository usersRepository) {
        this.userHouseRepository = userHouseRepository;
        this.houseRepository = houseRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public boolean existsMemberByMemberId(long houseId, String username) throws EntityException {
        ValidationsUtils.validateHouseId(houseId);
        ValidationsUtils.validateUserUsername(username);
        return userHouseRepository.existsById_HouseIdAndUsersByUsersId_UsersUsername(houseId, username);
    }

    @Override
    public UserHouse getMemberByMemberId(long houseId, String username) throws EntityException, EntityNotFoundException {
        ValidationsUtils.validateHouseId(houseId);
        ValidationsUtils.validateUserUsername(username);
        return userHouseRepository
                .findById_HouseIdAndUsersByUsersId_UsersUsername(houseId, username)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_EXIST));
    }

    @Override
    public List<UserHouse> getMembersByHouseId(long houseId) throws EntityException, EntityNotFoundException {
        // TODO transacional?
        ValidationsUtils.validateHouseId(houseId);
        checkHouse(houseId);
        return userHouseRepository.findAllById_HouseId(houseId);
    }

    @Override
    public UserHouse associateMember(long houseId, String username, Boolean administrator) throws EntityException, EntityNotFoundException {
        // TODO transacional?
        ValidationsUtils.validateUserUsername(username);
        Users user = usersRepository.findByUsersUsername(username).orElseThrow(() -> new EntityNotFoundException(USER_NOT_EXIST));
        UserHouse member = new UserHouse(houseId, user.getUsersId(), administrator);
        checkHouse(houseId);
        userHouseRepository.save(member);
        return member;
    }

    @Transactional
    @Override
    public void deleteMemberByMemberId(long houseId, String username) throws EntityException, EntityNotFoundException {
        UserHouse member = getMemberByMemberId(houseId, username);
        userHouseRepository.deleteById(member.getId());
    }

    private void checkHouse(long houseId) throws EntityNotFoundException {
        if (!houseRepository.existsById(houseId))
            throw new EntityNotFoundException(HOUSE_NOT_EXIST);
    }
}
