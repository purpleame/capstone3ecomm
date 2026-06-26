package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Profile;
import org.yearup.repository.ProfileRepository;

@Service
public class ProfileService
{
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository)
    {
        this.profileRepository = profileRepository;
    }

    public Profile create(Profile profile)
    {
        return profileRepository.save(profile);
    }

    public Profile getProfile(int userId) {
        return profileRepository.findById(userId).orElseThrow();
    }

    public Profile updateProfile(int userId, Profile profile) {
        Profile existing = profileRepository.findById(userId).orElseThrow();
        existing.setFirstName(profile.getFirstName());
        existing.setLastName(profile.getLastName());
        existing.setCity(profile.getCity());
        existing.setEmail(profile.getEmail());
        existing.setAddress(profile.getAddress());
        existing.setZip(profile.getZip());
        existing.setPhone(profile.getPhone());
        existing.setState(profile.getState());
        return profileRepository.save(existing);
    }
}
