package io.github.thecsdev.tcdcommons.util;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;
import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.TCDCommons;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * {@link TCDCommons}'s {@link Text}s.
 * @apiNote Contains all translatable {@link Text}s used by this mod.
 */
public final @Internal class TCDCT
{
	// ==================================================
	private TCDCT() {}
	// ==================================================
	public static final MutableText tcdc() { return translatable("tcdcommons"); }
	// --------------------------------------------------
	public static final MutableText tcdc_term_ghSponsors()            { return translatable("tcdcommons.github.sponsors"); }
	public static final MutableText tcdc_term_ghSponsors_noSponsors() { return translatable("tcdcommons.github.sponsors.no_sponsors"); }
	public static final MutableText tcdc_term_specialThanks()         { return translatable("tcdcommons.special_thanks"); }
	public static final MutableText tcdc_term_featured()              { return translatable("tcdcommons.featured"); }
	public static final MutableText tcdc_term_featured_noOne()        { return translatable("tcdcommons.featured.no_one"); }
	public static final MutableText tcdc_term_fetchFail()             { return translatable("tcdcommons.fetch_fail"); }
	//
	public static final MutableText tcdc_term_clientSide() { return translatable("tcdcommons.client_side"); }
	public static final MutableText tcdc_term_serverSide() { return translatable("tcdcommons.server_side"); }
	// --------------------------------------------------
	public static final MutableText pbadge_title()        { return translatable("tcdcommons.api.badge.playerbadge"); }
	public static final MutableText pbadge_title_plural() { return translatable("tcdcommons.api.badge.playerbadge.plural"); }
	// --------------------------------------------------
	public static final MutableText gui_explorer_title()        { return translatable("tcdcommons.api.client.gui.screen.explorer.tfilechooserscreen.title"); }
	public static final MutableText gui_explorer_title_save()   { return translatable("tcdcommons.api.client.gui.screen.explorer.tfilechooserscreen.title_save"); }
	public static final MutableText gui_explorer_title_open()   { return translatable("tcdcommons.api.client.gui.screen.explorer.tfilechooserscreen.title_open"); }
	public static final MutableText gui_explorer_title_selDir() { return translatable("tcdcommons.api.client.gui.screen.explorer.tfilechooserscreen.title_seldir"); }
	//
	public static final MutableText gui_explorer_actionBar_fileName() { return translatable("tcdcommons.api.client.gui.panel.explorer.actionbar.file_name"); }
	public static final MutableText gui_explorer_actionBar_fileType() { return translatable("tcdcommons.api.client.gui.panel.explorer.actionbar.file_type"); }
	public static final MutableText gui_explorer_actionBar_saveFile() { return translatable("tcdcommons.api.client.gui.panel.explorer.actionbar.save_file"); }
	public static final MutableText gui_explorer_actionBar_openFile() { return translatable("tcdcommons.api.client.gui.panel.explorer.actionbar.open_file"); }
	//
	public static final MutableText gui_explorer_fileFilter_allFiles()               { return translatable("tcdcommons.api.util.interfaces.tfilefilter.all_files"); }
	public static final MutableText gui_explorer_fileFilter_extFiles(Text extension) { return translatable("tcdcommons.api.util.interfaces.tfilefilter.x_files", extension); }
	//
	public static final MutableText gui_explorer_fileList_errNoSuchDir()       { return translatable("This directory does not exist..."); }
	public static final MutableText gui_explorer_fileList_errNoAccess()        { return translatable("No permission to read this directory..."); }
	public static final MutableText gui_explorer_fileList_errOther(Text error) { return translatable("tcdcommons.api.client.gui.panel.explorer.filelistpanel.err_other", error); }
	// --------------------------------------------------
	public static final MutableText gui_wSelect_defLabel() { return translatable("tcdcommons.api.client.gui.widget.tselectwidget.default_label"); }
	// --------------------------------------------------
	public static final MutableText cmd_pb_edit_out(Text badge, Text affectedPlayerCount) { return translatable("commands.badges.edit.output", badge, affectedPlayerCount); }
	public static final MutableText cmd_pb_clear_out(Text affectedPlayerCount)            { return translatable("commands.badges.clear.output", affectedPlayerCount); }
	public static final MutableText cmd_pb_chatGrant(Text player, Text badge)             { return translatable("commands.badges.chat_grant", player, badge); }
	public static final MutableText cmd_pb_query_out(Text player, Text badge, Text value) { return translatable("commands.badges.query.output", player, badge, value); }
	// ==================================================
}