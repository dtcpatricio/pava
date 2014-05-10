package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
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
					"	ist.meic.pa.ObjectTracer.addObject($_).setConstructor(" +
					"		\"%s\", \"%s\", \"%s\");" +
					"}";

	// TODO: Maybe use addMethod here
	private final String methodReturnTemplate = 
			"{" + 
					"if($args != null) { " +
					"	ist.meic.pa.ObjectTracer.addArgumentsMethod($args, \"%s\", \"%s\", \"%s\");}" + 
					"" +
					"	$_ = $proceed($$); " +
					"	if($_ != null) {" +
					"		ist.meic.pa.ObjectTracer.getTraceObject($_).addMethod(" +
					"			%s, \"%s\", \"%s\", \"%s\"); }" +
					"}";

	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
	CannotCompileException {
		CtClass ctClass = pool.get(className);

		// TODO confirmar que não é uma classe pertencente ao nosso programa
		if(className.equals("ist.meic.pa.Test.Test2")) {
			Trace(ctClass);
		}
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
	CannotCompileException {
		// TODO Auto-generated method stub
	}

	// TODO: falta iterar por todos os campos
	public void Trace(CtClass ctClass) throws CannotCompileException, NotFoundException {
		for(final CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			NewExprTracer(ctMethod);
			//FieldAccessTracer(ctMethod);
			MethodCallTracer(ctMethod);
		}		
	}	

	// PROBLEM: object added hasn't a reference, because $_ only returns for instance 3 in int i = 3
	public void FieldAccessTracer(CtMethod ctMethod) throws CannotCompileException {
		ctMethod.instrument(new ExprEditor() {
			public void edit(FieldAccess fieldAccess) 
					throws CannotCompileException {
				if(fieldAccess.isWriter()) {
					System.err.println("Class: " + fieldAccess.toString() +
									   " Line: " + fieldAccess.getLineNumber() + 
									   " File: " + fieldAccess.getFileName());
					fieldAccess.replace(String.format(
							constructorTemplate, 
							fieldAccess.toString(),
							fieldAccess.getLineNumber(), 
							fieldAccess.getFileName()));
				}
			}
		});
	}

	// For new expressions
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
				if(!(methodCall.getClassName().contains("ist.meic.pa.ObjectTracer") ||
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
