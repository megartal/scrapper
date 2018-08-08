package com.name.services;

import com.name.documents.Proxy;
import com.name.repositories.ProxyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Akbar
 * @DATE 8/8/2018.
 */
@Service
public class ProxyService {
    private final ProxyRepository proxyRepository;

    public ProxyService(ProxyRepository proxyRepository) {
        this.proxyRepository = proxyRepository;
    }


    public List<Proxy> getProxies() {
        return proxyRepository.findAll();
    }


    public List<Proxy> getHttpsProxies() {
        return proxyRepository.findByProtocol("https");
    }

    public List<Proxy> getHttpProxies() {
        return proxyRepository.findByProtocol("http");
    }
}
