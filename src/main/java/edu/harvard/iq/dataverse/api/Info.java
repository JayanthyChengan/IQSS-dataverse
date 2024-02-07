package edu.harvard.iq.dataverse.api;

import edu.harvard.iq.dataverse.settings.JvmSettings;
import edu.harvard.iq.dataverse.settings.SettingsServiceBean;
import edu.harvard.iq.dataverse.util.SystemConfig;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

@Path("info")
public class Info extends AbstractApiBean {

    @EJB
    SettingsServiceBean settingsService;
    
    @EJB
    SystemConfig systemConfig;

    @GET
    @Path("settings/:DatasetPublishPopupCustomText")
    public Response getDatasetPublishPopupCustomText() {
        return getSettingResponseByKey(SettingsServiceBean.Key.DatasetPublishPopupCustomText);
    }

    @GET
    @Path("settings/:MaxEmbargoDurationInMonths")
    public Response getMaxEmbargoDurationInMonths() {
        return getSettingResponseByKey(SettingsServiceBean.Key.MaxEmbargoDurationInMonths);
    }

    @GET
    @Path("version")
    public Response getInfo() {
        String versionStr = systemConfig.getVersion(true);
        String[] comps = versionStr.split("build",2);
        String version = comps[0].trim();
        JsonValue build = comps.length > 1 ? Json.createArrayBuilder().add(comps[1].trim()).build().get(0) : JsonValue.NULL;
        return ok(Json.createObjectBuilder()
                .add("version", version)
                .add("build", build));
    }

    @GET
    @Path("server")
    public Response getServer() {
        return ok(JvmSettings.FQDN.lookup());
    }

    @GET
    @Path("apiTermsOfUse")
    public Response getTermsOfUse() {
        return ok(systemConfig.getApiTermsOfUse());
    }

    @GET
    @Path("settings/incompleteMetadataViaApi")
    public Response getAllowsIncompleteMetadata() {
        return ok(JvmSettings.API_ALLOW_INCOMPLETE_METADATA.lookupOptional(Boolean.class).orElse(false));
    }

    @GET
    @Path("zipDownloadLimit")
    public Response getZipDownloadLimit() {
        long zipDownloadLimit = SystemConfig.getLongLimitFromStringOrDefault(settingsSvc.getValueForKey(SettingsServiceBean.Key.ZipDownloadLimit), SystemConfig.defaultZipDownloadLimit);
        return ok(zipDownloadLimit);
    }

    private Response getSettingResponseByKey(SettingsServiceBean.Key key) {
        String setting = settingsService.getValueForKey(key);
        if (setting != null) {
            return ok(Json.createObjectBuilder().add("message", setting));
        } else {
            return notFound("Setting " + key + " not found");
        }
    }


    @GET
    @Path("idpignorelist")
    public Response getShibInstitutionIgnoreList() {
        JsonArrayBuilder arrBld = Json.createArrayBuilder();
        String idpCSV = settingsService.getValueForKey(SettingsServiceBean.Key.ShibInstitutionIgnoreList);
        if (idpCSV != null) {
            List<String> shibIdpIgnoreList = Arrays.asList(idpCSV.split("\\|"));
            shibIdpIgnoreList.forEach(idp -> arrBld.add(idp));
            return ok(arrBld);
        } else {
            return null;
        }
    }
}
