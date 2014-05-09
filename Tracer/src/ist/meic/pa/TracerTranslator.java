package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

// TODO: Not sure if create 2 different Translators.
// One responsible for constructors and another one for method calls
public class TracerTranslator implements Translator {

	// TODO: Maybe store templates in different file
	private final String constructorTemplate = 
			"{" +
					"	$_ = $proceed($$); " +
					"	ist.meic.pa.IdentityTracer.addObject($_);" + 
					"	ist.meic.pa.IdentityTracer.getTraceObject($_).setConstructor(" +
					"		\"%s\", \"%s\", \"%s\");" +
					"}";

	private final String methodReturnTemplate = 
			"{" + 
					"if($args != null) { " +
					"ist.meic.pa.IdentityTracer.addArguments($args, \"%s\", \"%s\", \"%s\");}" + 
					"" +
					"	$_ = $proceed($$); " +
					"	if($_ != null) {" +
					"		ist.meic.pa.IdentityTracer.getTraceObject($_).addMethod(" +
					"			%s, \"%s\", \"%s\", \"%s\"); }" +
					"}";

	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
	CannotCompileException {
		CtClass ctClass = pool.get(className);

		// TODO confirmar que não é uma classe pertencente ao nosso programa
		if(className.equals("ist.meic.pa.Test.Test")) {
			Trace(ctClass);
		}
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
	CannotCompileException {
		// TODO Auto-generated method stub
	}

	
	// TODO: falta iterar por todos os campos
	public void Trace(CtClass ctClass) throws CannotCompileException {
		for(final CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			NewExprTracer(ctMethod);
			MethodCallTracer(ctMethod);
		}
	}

	// For new expressions
	// TODO: Consider field initializations such as private int i = 3
	public void NewExprTracer(CtMethod ctMethod) throws CannotCompileException {
		ctMethod.instrument(new ExprEditor() {
			public void edit(NewExpr newExpr) 
					throws CannotCompileException {
				try {
					
					newExpr.replace(String.format(
							constructorTemplate, 
							newExpr.getConstructor().getLongName(),
							newExpr.getLineNumber(), 
							newExpr.getFileName()));
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void MethodCallTracer(final CtMethod ctMethod) throws CannotCompileException {		
		ctMethod.instrument(new ExprEditor() {
			public void edit(MethodCall methodCall) 
					throws CannotCompileException {
				// Shouldn't be like this, just for testing
				// Don't want to evaluate our classes
				if(!(methodCall.getClassName().contains("ist.meic.pa.IdentityTracer") ||
						methodCall.getClassName().contains("ist.meic.pa.TraceObject"))) {
					try {
						// TODO: get method name with enclosing class
						methodCall.replace(String.format(methodReturnTemplate,
								methodCall.getMethod().getLongName(),
								methodCall.getLineNumber(), 
								methodCall.getFileName(),
								"false",
								methodCall.getMethod().getLongName(),
								methodCall.getLineNumber(), 
								methodCall.getFileName()));
					} catch (NotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}
}
