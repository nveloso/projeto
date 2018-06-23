package pt.isel.ps.gis.bll.implementations;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.ps.gis.bll.HouseAllergyService;
import pt.isel.ps.gis.dal.repositories.AllergyRepository;
import pt.isel.ps.gis.dal.repositories.HouseAllergyRepository;
import pt.isel.ps.gis.dal.repositories.HouseRepository;
import pt.isel.ps.gis.dal.repositories.UserHouseRepository;
import pt.isel.ps.gis.exceptions.EntityException;
import pt.isel.ps.gis.exceptions.EntityNotFoundException;
import pt.isel.ps.gis.model.Characteristics;
import pt.isel.ps.gis.model.House;
import pt.isel.ps.gis.model.HouseAllergy;
import pt.isel.ps.gis.model.HouseAllergyId;
import pt.isel.ps.gis.utils.ValidationsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HouseAllergyServiceImpl implements HouseAllergyService {

    private static final String HOUSE_NOT_EXIST = "House does not exist.";
    private static final String ALLERGEN_NOT_EXIST = "Allergen does not exist.";
    private static final String ALLERGY_NOT_EXIST = "Allergy does not exist.";

    private final HouseAllergyRepository houseAllergyRepository;
    private final UserHouseRepository membersRepository;
    private final HouseRepository houseRepository;
    private final AllergyRepository allergyRepository;

    public HouseAllergyServiceImpl(HouseAllergyRepository houseAllergyRepository, UserHouseRepository membersRepository, HouseRepository houseService, AllergyRepository allergyRepository) {
        this.houseAllergyRepository = houseAllergyRepository;
        this.membersRepository = membersRepository;
        this.houseRepository = houseService;
        this.allergyRepository = allergyRepository;
    }

    @Override
    public boolean existsHouseAllergyByHouseAllergyId(long houseId, String allergy) throws EntityException {
        return houseAllergyRepository.existsById(new HouseAllergyId(houseId, allergy));
    }

    @Override
    public List<HouseAllergy> getAllergiesByHouseId(long houseId) throws EntityException, EntityNotFoundException {
        // TODO é transacional? tá a fazer uma verificacao e dps e um get
        checkHouse(houseId);
        return houseAllergyRepository.findAllById_HouseId(houseId);
    }

    @Transactional
    @Override
    public List<HouseAllergy> associateHouseAllergies(long houseId, HouseAllergy[] allergies) throws EntityNotFoundException, EntityException {
        List<HouseAllergy> houseAllergies = new ArrayList<>();
        for (HouseAllergy houseAllergy : allergies) {
            houseAllergies.add(associateHouseAllergy(houseId, houseAllergy.getId().getAllergyAllergen(), houseAllergy.getHouseallergyAllergicsnum()));
        }
        return houseAllergies;
    }

    @Transactional
    @Override
    public HouseAllergy associateHouseAllergy(long houseId, String allergen, Short allergicsNum) throws EntityNotFoundException, EntityException {
        ValidationsUtils.validateHouseAllergyAllergicsNum(allergicsNum);
        // Total Membros na casa
        short totalMembers = getTotalHouseMembers(houseId);
        if (allergicsNum > totalMembers)
            throw new EntityException(String.format("Cannot add allergy in the house wih ID %d, there are more allergics than members in the house.",
                    houseId));
        Optional<HouseAllergy> optionalHouseAllergy = houseAllergyRepository.findById(new HouseAllergyId(houseId, allergen));
        HouseAllergy houseAllergy;
        if (optionalHouseAllergy.isPresent()) {
            houseAllergy = optionalHouseAllergy.get();
            houseAllergy.setHouseallergyAllergicsnum(allergicsNum);
        } else {
            checkHouse(houseId);
            checkAllergy(allergen);
            houseAllergy = new HouseAllergy(houseId, allergen, allergicsNum);
            houseAllergyRepository.save(houseAllergy);
        }
        return houseAllergy;
    }

    @Override
    public void deleteHouseAllergyByHouseAllergyId(long houseId, String allergen) throws EntityException, EntityNotFoundException {
        HouseAllergyId id = new HouseAllergyId(houseId, allergen);
        checkAllergen(houseId, allergen);
        houseAllergyRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteAllHouseAllergiesByHouseId(long houseId) throws EntityException, EntityNotFoundException {
        checkHouse(houseId);
        houseAllergyRepository.deleteAllById_HouseId(houseId);
    }

    private void checkHouse(long houseId) throws EntityException, EntityNotFoundException {
        ValidationsUtils.validateHouseId(houseId);
        if (!houseRepository.existsById(houseId))
            throw new EntityNotFoundException(HOUSE_NOT_EXIST);
    }

    private void checkAllergy(String allergy) throws EntityNotFoundException {
        if (!allergyRepository.existsById(allergy))
            throw new EntityNotFoundException(ALLERGY_NOT_EXIST);
    }

    private void checkAllergen(long houseId, String allergen) throws EntityException, EntityNotFoundException {
        if (!existsHouseAllergyByHouseAllergyId(houseId, allergen))
            throw new EntityNotFoundException(ALLERGEN_NOT_EXIST);
    }

    private short getTotalHouseMembers(long houseId) throws EntityNotFoundException {
        House house = houseRepository.findById(houseId).orElseThrow(() -> new EntityNotFoundException(HOUSE_NOT_EXIST));
        Characteristics characteristics = house.getHouseCharacteristics();
        return (short) (characteristics.getHouse_babiesNumber() + characteristics.getHouse_childrenNumber()
                        + characteristics.getHouse_adultsNumber() + characteristics.getHouse_seniorsNumber());
    }
}
