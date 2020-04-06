package cn.zhiu.framework.base.api.core.annotation.exception;

import com.google.common.collect.Maps;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * The type Exception code processor.
 *
 * @author zhuzz
 * @time 2019 /04/02 14:57:02
 */
public class ExceptionCodeProcessor extends AbstractProcessor {

    private Messager messager;
    private static Map<String, String> codeSet = Maps.newHashMap();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        System.err.println("ExceptionCodeProcessor Run");
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.err.println("ExceptionCodeProcessor Process");
        for (Element em : roundEnv.getElementsAnnotatedWith(ExceptionCode.class)) {
            if (!em.getKind().equals(ElementKind.CLASS)) {
                error(em, "错误的注解类型, 只有类对象能够被该 @%s 注解处理", ExceptionCode.class.getSimpleName());
                return true;
            }

            TypeElement typeElement = (TypeElement) em;
            ExceptionCode exceptionCode = em.getAnnotation(ExceptionCode.class);
            if (exceptionCode == null) {
                error(em, "@%s 注解未找到", ExceptionCode.class.getSimpleName());
                return true;
            }
            if (exceptionCode.code() != null && !"".equals(exceptionCode.code())) {
                String code = exceptionCode.code();
                if (!checkCodeFormat(code)) {
                    error(em, "code码 %s 格式不对", code);
                }
                if (!codeSet.containsKey(code)) {
                    codeSet.put(code, typeElement.getQualifiedName().toString());
                } else {
                    error(em, "code码 %s 在 %s 中已经存在", code, typeElement.getQualifiedName().toString());
                }
            }
        }
        return false;
    }

    private boolean checkCodeFormat(String code) {
        if (code.length() != 6) {
            return false;
        }
        if (!(!Character.isLowerCase(code.charAt(0)) && !Character.isLowerCase(code.charAt(1)))) {
            return false;
        }
        for (int i = 2; i < 6; i++) {
            try {
                Integer.parseInt(String.valueOf(code.charAt(i)));
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new LinkedHashSet<>();
        supportedAnnotationTypes.add("cn.zhiu.framework.base.api.core.annotation.exception.ExceptionCode");
        return supportedAnnotationTypes;
    }

    protected void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }
}
