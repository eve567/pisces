package net.ufrog.pisces.service.impls;

import net.ufrog.pisces.service.AppService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应用业务实现
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Service
@Transactional(readOnly = true)
public class AppServiceImpl implements AppService {
}
