package com.ib.web.controller.umkm;

import com.ib.web.dto.umkm.MerchantDto;
import com.ib.web.dto.umkm.OutletDto;
import com.ib.web.service.umkm.MerchantClientService;
import com.ib.web.service.umkm.OutletClientService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
public class ApiMerchantController {
    private final MerchantClientService merchantClientService;
    private final OutletClientService outletClientService;

    public ApiMerchantController(MerchantClientService merchantClientService, OutletClientService outletClientService) {
        this.merchantClientService = merchantClientService;
        this.outletClientService = outletClientService;
    }

    @GetMapping("/mine")
    public List<MerchantDto> merchantsMine(Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        return merchantClientService.getMerchantsByRole(jwt);
    }

    @GetMapping("/{merchantId}/outlets")
    public List<OutletDto> byMerchantId(Authentication authentication,
                                        @PathVariable Long merchantId) {
        String jwt = (String) authentication.getCredentials();
        return outletClientService.getOutletsByMerchantId(merchantId, jwt);
    }
}
