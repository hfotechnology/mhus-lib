package de.mhus.lib.sql.parser;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import de.mhus.lib.core.MString;
import de.mhus.lib.core.parser.ConstantPart;
import de.mhus.lib.core.parser.ParseException;
import de.mhus.lib.core.parser.ParseReader;
import de.mhus.lib.core.parser.ParsingPart;
import de.mhus.lib.core.parser.StringParsingPart;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.MRuntimeException;


/**
 * <p>MainPart class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class MainPart extends StringParsingPart {

	private LinkedList<ParsingPart> parts = new LinkedList<ParsingPart>();
	private boolean stopOnComma;
	private int brakCount;
	private ParsingPart last;
	private boolean parseAttributes = true;
	private ICompiler compiler;

	/**
	 * <p>Constructor for MainPart.</p>
	 *
	 * @param compiler a {@link de.mhus.lib.sql.parser.ICompiler} object.
	 */
	public MainPart(ICompiler compiler) {
		this.compiler = compiler;
		setParseAttributes(compiler.isParseAttributes());
	}

	/** {@inheritDoc} */
	@Override
	public void execute(StringBuffer out, Map<String, Object> attributes) {
		try {
			for (ParsingPart p : parts) {
				p.execute(out, attributes);
			}
		} catch (MException e) {
			throw new MRuntimeException(e);
		}
	}

	/**
	 * <p>add.</p>
	 *
	 * @param pp a {@link de.mhus.lib.core.parser.ParsingPart} object.
	 */
	public void add(ParsingPart pp) {
		parts.add(pp);
		last = pp;
	}

	/** {@inheritDoc} */
	@Override
	public void doPreParse() {
	}

	/** {@inheritDoc} */
	@Override
	public void doPostParse() {
	}

	/** {@inheritDoc} */
	@Override
	public boolean parse(char c, ParseReader str) throws ParseException, IOException {

		if (stopOnComma && ( c == ',' || ( c == ')' && brakCount == 0 ) ) ) {
			return false;
		} else
			if (stopOnComma && c == '(') {
				ParsingPart pp = new OnePart(compiler);
				add(pp);
				pp.parse(str);
				brakCount++;
				return true;
			} else
				if (stopOnComma && c == ')') {
					ParsingPart pp = new OnePart(compiler);
					add(pp);
					pp.parse(str);
					brakCount--;
					return true;
				} else
					if (c >= '0' && c <= '9') {
						ParsingPart pp = new NumberPart(compiler);
						add(pp);
						pp.parse(str);
						return true;
					} else
						if (   c == '-' || c == '+' || c == '(' || c == ')' || c == ' ' || c == '\n'
						|| c == '\r' || c == '\t' || c == '*' || c == '=' || c == '>' || c == '<'
						|| c == '!' || c == '/' || c == ',' || c == '.' || c == '|' || c == '&' || c == '%') {

							if (last != null && last instanceof OnePart) {
								((OnePart)last).append(c);
								str.consume();
							} else {
								ParsingPart pp = new OnePart(compiler);
								add(pp);
								pp.parse(str);
							}
							return true;
						} else
							if ( (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_' ) {
								ConstWordPart pp = new ConstWordPart(compiler);
								pp.parse(str);
								// remove spaces - not allowed yet
								//			boolean consumed;
								//			do {
								//				consumed = false;
								//				char c2 = str.character();
								//				if (c2 == ' ' || c2 == '\t') {
								//					str.consume();
								//					consumed = true;
								//				}
								//			} while (consumed);
								// it's a function?
								if (!str.isClosed() && str.character() == '(') {
									ParsingPart pp2 = new FunctionPart(compiler,pp.getContent());
									str.consume(); // consume ( on this place
									pp2.parse(str);
									pp2 = compiler.compileFunction((FunctionPart) pp2);
									add(pp2);
								} else {
									add(pp);
								}
								return true;
							} else
								//		if (c == '(') {
								//			str.consume();
								//			ParsingPart pp = new MyParsingPart();
								//			add(pp);
								//			pp.parse(str);
								//			return true;
								//		} else
								//		if (c == ')') {
								//			str.consume();
								//			return false;
								//		} else
								if (c == '\'' || c == '"') {
									ParsingPart pp = new QuotPart(compiler);
									add(pp);
									pp.parse(str);
									return true;
								} else
									if (isParseAttributes() && c == '$') {
										str.consume();
										if (str.isClosed()) {
											add(new ConstantPart("$"));
											return false;
										}
										if (str.character() == '$') {
											str.consume();
											add(new ConstantPart("$"));
											return true;
										}
										ParsingPart pp = new ParameterPart(compiler);
										add(pp);
										pp.parse(str);
										return true;
									}

		throw new ParseException("unknown character",c,str.getPosition() ); // TODO more info
	}

	/** {@inheritDoc} */
	@Override
	public void dump(int level, StringBuffer out) {
		MString.appendRepeating(level, ' ', out);
		out.append(getClass().getCanonicalName()).append(" (").append("\n");
		for (ParsingPart p : parts) {
			p.dump(level+1, out);
		}
		MString.appendRepeating(level, ' ', out);
		out.append(")").append("\n");
	}

	/**
	 * <p>Setter for the field <code>stopOnComma</code>.</p>
	 *
	 * @param b a boolean.
	 */
	public void setStopOnComma(boolean b) {
		stopOnComma = b;
	}

	/**
	 * <p>Setter for the field <code>parseAttributes</code>.</p>
	 *
	 * @param parseAttributes a boolean.
	 */
	public void setParseAttributes(boolean parseAttributes) {
		this.parseAttributes = parseAttributes;
	}

	/**
	 * <p>isParseAttributes.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isParseAttributes() {
		return parseAttributes;
	}

}
