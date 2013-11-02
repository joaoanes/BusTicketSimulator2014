package pt.feup.busticket.tickets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

public abstract class Ticket implements Serializable {
	String id;
	Date validated;
	int bus = -1;
	int user_id = -1;

	public static final int TYPE_T1 = 1;
	public static final int TYPE_T2 = 2;
	public static final int TYPE_T3 = 3;

	public static final String FIELD_TICKETS = "tickets";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_VALIDATED = "validated";
	public static final String FIELD_BUS = "bus";
	public static final String FIELD_ID = "id";
	public static final String FIELD_UID = "uid";

	public Ticket(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isValidated() {
		return validated != null;
	}

	public Date getValidated() {
		return validated;
	}

	public void setValidated(Date validated) {
		this.validated = validated;
	}

	public int getBus() {
		return bus;
	}

	public void setBus(int bus) {
		this.bus = bus;
	}

	public int getUserId() {
		return user_id;
	}

	public void setUserId(int user_id) {
		this.user_id = user_id;
	}

	@Override
	public String toString() {
		return "id: " + id +
				"\n type: " + getType() +
				"\n validate: " + (validated != null ? validated.toString() : "-1") +
				"\n bus: " + getBus() +
				"\n user_id: " + getUserId();
	}

	abstract public double getPrice();
	abstract public String getType();
	abstract public int getDuration();

	public String serialize() {
		/*try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(ticket);
			so.flush();
			return bo.toString();
			//return Base64.encodeToString(bo.toByteArray(), Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}*/
		return id + "|" + getType() + "|" + (validated != null ? validated.getTime() : "-1" )+ "|" + bus + "|" + user_id;
	}

	public static Ticket deserialize(String ticket_string) {
		String[] values = ticket_string.split("\\|");
		Ticket ticket = null;
		if(values[1].equals("T1")) {
			ticket = new T1(values[0]);
		}
		else if(values[1].equals("T2")) {
			ticket = new T2(values[0]);
		}
		else if(values[1].equals("T3")) {
			ticket = new T3(values[0]);
		}
		
		long date = Long.parseLong(values[2]);
		int bus = Integer.parseInt(values[3]);
		int user_id = Integer.parseInt(values[4]);
		
		if(date > -1) {
			ticket.setValidated(new Date(date));
		}
		ticket.setBus(bus);
		ticket.setUserId(user_id);
		return ticket;
	}


	public static Ticket getTicketFromJSON(JSONObject json_ticket) {
		String type = json_ticket.optString(FIELD_TYPE);
		String id = json_ticket.optString(FIELD_ID);
		if(/*(type < 1 || type > 3) && */id.equals("")) {
			return null;
		}

		Ticket ticket = null;

		/*switch (type) {
			case 1:
				ticket = new T1(id);
				break;
			case 2:
				ticket = new T2(id);
				break;
			case 3:
				ticket = new T3(id);
				break;
			default:
				return null;
		}*/
		if(type.equals("T1")) {
			ticket = new T1(id);
		}
		else if(type.equals("T2")) {
			ticket = new T2(id);
		}
		else if(type.equals("T3")) {
			ticket = new T3(id);
		}
		else {
			return null;
		}

		int user_id = json_ticket.optInt(FIELD_UID, -1);
		ticket.setUserId(user_id);

		String date = json_ticket.optString(FIELD_VALIDATED);
		if(!(date.equals("") || date.equals("null") )) {
			Date validated_date = new Date(date);
			ticket.setValidated(validated_date);

			int bus = json_ticket.optInt(FIELD_BUS, -1);
			if(bus < 0) {
				return null;
			}
			ticket.setBus(bus);

		}

		return ticket;
	}

	public static ArrayList<Ticket> getTicketsFromJSON(String json_string) {
		ArrayList<Ticket> tickets = null;

		try {
			JSONObject json = new JSONObject(json_string);
			JSONArray json_tickets = json.getJSONArray(FIELD_TICKETS);
			tickets = new ArrayList<Ticket>();

			for(int i = 0; i < json_tickets.length(); ++i) {
				JSONObject json_ticket = json_tickets.getJSONObject(i);

				Ticket ticket = getTicketFromJSON(json_ticket);

				if(ticket != null) {
					tickets.add(ticket);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return tickets;
	}

	public static Bundle getBuyResults(String json_string) {
		Bundle result = new Bundle();
		try {
			JSONObject json = new JSONObject(json_string);
			result.putInt("PRICE", json.optInt("price"));
			result.putInt("T1", json.optInt("T1"));
			result.putInt("T2", json.optInt("T2"));
			result.putInt("T3", json.optInt("T3"));

			String extra = json.optString("extra");
			if(!extra.equals("")) {
				result.putString("EXTRA", extra);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

}
