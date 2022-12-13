package com.zhao.importAnnotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Import
 *
 * @author zhaojinliang
 * @version 1.0.0
 * @since 2022/12/12
 */
public class ZhaoImportSelector implements ImportSelector {
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[]{ZhaoImportService.class.getName()};
	}
}
