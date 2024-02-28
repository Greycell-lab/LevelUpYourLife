package de.domesoft.levelupapi.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@SuppressWarnings("unused")
@Service
public class ParentService {
    @Autowired
    public ParentService(ParentRepository parentRepository) {
    }
}
